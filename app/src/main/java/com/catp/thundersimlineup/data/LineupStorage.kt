package com.catp.thundersimlineup.data

import android.content.Context
import com.catp.model.JsonLineupConfig
import com.catp.thundersimlineup.data.db.DBPopulate
import com.catp.thundersimlineup.data.db.LineupDao
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
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
    fun refresh(context: Context, force: Boolean): Observable<RefreshResult> {
        return Observable.just(context)
            .observeOn(Schedulers.io())
            .map { ctx ->
                val localJsonConfig = storage.loadFromRAW(ctx)
                val localUpdateResult = updateDBIfNeeded(localJsonConfig, ctx)
                if (localUpdateResult == RefreshResult.NO_NEW_DATA
                    && (force || refreshIntervalChecker.isRefreshNeeded(context))
                ) {
                    return@map updateDBIfNeeded(netLoader.getData(), ctx)
                } else {
                    localUpdateResult
                }
            }
    }

    private fun updateDBIfNeeded(jsonConfig: JsonLineupConfig, ctx: Context): RefreshResult {
        val localDBVersion = lineupDao.getVersion()?.version ?: 0
        // check if local json version is grater than db
        return if (jsonConfig.version > localDBVersion) {
            // if so update db
            dbPopulate.updateData(jsonConfig, ctx)
            RefreshResult.NEW_DATA
        } else
            RefreshResult.NO_NEW_DATA
    }

    enum class RefreshResult {
        NEW_DATA,
        NO_NEW_DATA
    }


}