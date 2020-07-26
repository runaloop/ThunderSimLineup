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
    fun refresh(context: Context, force: Boolean): Observable<REFRESH_RESULT> {
        return Observable.just(context)
            .observeOn(Schedulers.io())
            .map { ctx ->
                val localJsonConfig = storage.loadFromRAW(ctx)
                val localUpdateResult = updateDBIfNeeded(localJsonConfig, ctx)
                if (localUpdateResult == REFRESH_RESULT.NO_NEW_DATA
                    && (force || refreshIntervalChecker.isRefreshNeeded(context))
                ) {
                    return@map updateDBIfNeeded(netLoader.getData(), ctx)
                } else {
                    localUpdateResult
                }
            }
    }

    fun updateDBIfNeeded(jsonConfig: JsonLineupConfig, ctx: Context): REFRESH_RESULT {
        val localDBVersion = lineupDao.getVersion()?.version ?: 0
        // check if local json version is grater than db
        if (jsonConfig.version > localDBVersion) {
            // if so update db
            dbPopulate.updateData(jsonConfig, ctx)
            return REFRESH_RESULT.NEW_DATA
        } else
            return REFRESH_RESULT.NO_NEW_DATA
    }

    enum class REFRESH_RESULT {
        NEW_DATA,
        NO_NEW_DATA
    }


}