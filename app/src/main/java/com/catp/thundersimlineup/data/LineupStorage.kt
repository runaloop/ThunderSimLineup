package com.catp.thundersimlineup.data

import android.content.Context
import com.catp.model.JsonLineupConfig
import com.catp.thundersimlineup.data.db.DBPopulate
import com.catp.thundersimlineup.data.db.LineupDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


//@InjectConstructor
class LineupStorage {
    @Inject
    lateinit var netLoader: NetLoader

    @Inject
    lateinit var storage: Storage

    @Inject
    lateinit var refreshIntervalChecker: RefreshIntervalChecker

    @Inject
    lateinit var lineupDao: LineupDao

    @Inject
    lateinit var dbPopulate: DBPopulate

    /**
     * If force - try to load from network, and apply it to db
     * else check via refresh interval, needness for network call
     * if local db empty, load from local json, and than try to check from network
     */
    suspend fun refresh(context: Context, force: Boolean): RefreshResult =
        withContext(Dispatchers.IO){
            val localJsonConfig = storage.loadFromRAW(context)
            val localUpdateResult = updateDBIfNeeded(localJsonConfig)
            if (localUpdateResult == RefreshResult.NO_NEW_DATA
                && (force || refreshIntervalChecker.isRefreshNeeded(context))
            ) {
                updateDBIfNeeded(netLoader.getData())
            } else {
                localUpdateResult
            }
        }


    private fun updateDBIfNeeded(jsonConfig: JsonLineupConfig): RefreshResult {
        val localDBVersion = lineupDao.getVersion()?.version ?: 0
        // check if local json version is grater than db
        return if (jsonConfig.version > localDBVersion) {
            // if so update db
            dbPopulate.updateData(jsonConfig)
            RefreshResult.NEW_DATA
        } else
            RefreshResult.NO_NEW_DATA
    }

    enum class RefreshResult {
        NEW_DATA,
        NO_NEW_DATA
    }


}