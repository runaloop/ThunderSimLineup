package com.catp.thundersimlineup.data

import android.content.Context
import com.catp.thundersimlineup.data.db.DBPopulate
import com.catp.thundersimlineup.data.db.LineupDao
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import toothpick.InjectConstructor
import toothpick.ktp.delegate.inject


@InjectConstructor
class LineupStorage {
    private val netLoader by inject<NetLoader>()
    private val storage by inject<Storage>()
    private val refreshIntervalChecker by inject<RefreshIntervalChecker>()
    private val lineupDao  by inject<LineupDao>()
    private val dbPopulate by inject<DBPopulate>()
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
                val localDBVersion = lineupDao.getVersion()?.version ?: 0
                // check if local json version is grater than db
                if (localJsonConfig.version > localDBVersion) {
                    // if so update db
                    dbPopulate.updateData(localJsonConfig, ctx)
                    return@map REFRESH_RESULT.NEW_DATA
                } else if (!force && !refreshIntervalChecker.isRefreshNeeded(context)) {
                    //return local data
                    lineupDao.getLineups()
                } else {
                    //retrieve network data
                    netLoader.getData()
                }
                return@map REFRESH_RESULT.NO_NEW_DATA
            }
    }

    enum class REFRESH_RESULT {
        NEW_DATA,
        NO_NEW_DATA
    }


}