package com.support.bcRecievers

import android.content.Context
import android.net.NetworkInfo
import android.net.ConnectivityManager
import androidx.annotation.NonNull
import androidx.core.util.Preconditions


class Connectivity {
    var state: NetworkInfo.State? = null
    var detailedState: NetworkInfo.DetailedState? = null
    var type: Int = -1
    var subType: Int = -1
    var isAvailable: Boolean = false
    var isFailover: Boolean = false
    var isRoaming: Boolean = false
    var typeName: String?= ""
    var subTypeName: String= ""
    var reason: String= ""
    var extraInfo: String= ""

    protected constructor() {}

    protected constructor(builder: Builder) {
        state = builder.state
        detailedState = builder.detailedState
        type = builder.type
        subType = builder.subType
        isAvailable = builder.available
        isFailover = builder.failover
        isRoaming = builder.roaming
        typeName = builder.typeName
        subTypeName = builder.subTypeName
        reason = builder.reason
        extraInfo = builder.extraInfo
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val that = o as Connectivity?

        if (type != that!!.type) {
            return false
        }
        if (subType != that.subType) {
            return false
        }
        if (isAvailable != that.isAvailable) {
            return false
        }
        if (isFailover != that.isFailover) {
            return false
        }
        if (isRoaming != that.isRoaming) {
            return false
        }
        if (state != that.state) {
            return false
        }
        if (detailedState != that.detailedState) {
            return false
        }
        if (typeName != that.typeName) {
            return false
        }
        if (if (subTypeName != null) subTypeName != that.subTypeName else that.subTypeName != null) {
            return false
        }
        if (if (reason != null) reason != that.reason else that.reason != null) {
            return false
        }

        return if (extraInfo != null) extraInfo == that.extraInfo else that.extraInfo == null
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + (detailedState?.hashCode() ?: 0)
        result = 31 * result + type
        result = 31 * result + subType
        result = 31 * result + if (isAvailable) 1 else 0
        result = 31 * result + if (isFailover) 1 else 0
        result = 31 * result + if (isRoaming) 1 else 0
        result = 31 * result + typeName.hashCode()
        result = 31 * result + (subTypeName?.hashCode() ?: 0)
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + (extraInfo?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return ("Connectivity{"
                + "state="
                + state
                + ", detailedState="
                + detailedState
                + ", type="
                + type
                + ", subType="
                + subType
                + ", available="
                + isAvailable
                + ", failover="
                + isFailover
                + ", roaming="
                + isRoaming
                + ", typeName='"
                + typeName
                + '\''.toString()
                + ", subTypeName='"
                + subTypeName
                + '\''.toString()
                + ", reason='"
                + reason
                + '\''.toString()
                + ", extraInfo='"
                + extraInfo
                + '\''.toString()
                + '}'.toString())
    }

    class Builder {

        // disabling PMD for builder class attributes
        // because we want to have the same method names as names of the attributes for builder

        var state: NetworkInfo.State = NetworkInfo.State.DISCONNECTED // NOPMD
        var detailedState: NetworkInfo.DetailedState = NetworkInfo.DetailedState.IDLE // NOPMD
        var type = UNKNOWN_TYPE // NOPMD
        var subType = UNKNOWN_SUB_TYPE // NOPMD
        var available = false // NOPMD
        var failover = false // NOPMD
        var roaming = false // NOPMD
        var typeName = "NONE" // NOPMD
        var subTypeName = "NONE" // NOPMD
        var reason = "" // NOPMD
        var extraInfo = "" // NOPMD

        fun state(state: NetworkInfo.State): Builder {
            this.state = state
            return this
        }

        fun detailedState(detailedState: NetworkInfo.DetailedState): Builder {
            this.detailedState = detailedState
            return this
        }

        fun type(type: Int): Builder {
            this.type = type
            return this
        }

        fun subType(subType: Int): Builder {
            this.subType = subType
            return this
        }

        fun available(available: Boolean): Builder {
            this.available = available
            return this
        }

        fun failover(failover: Boolean): Builder {
            this.failover = failover
            return this
        }

        fun roaming(roaming: Boolean): Builder {
            this.roaming = roaming
            return this
        }

        fun typeName(name: String): Builder {
            this.typeName = name
            return this
        }

        fun subTypeName(subTypeName: String): Builder {
            this.subTypeName = subTypeName
            return this
        }

        fun reason(reason: String): Builder {
            this.reason = reason
            return this
        }

        fun extraInfo(extraInfo: String): Builder {
            this.extraInfo = extraInfo
            return this
        }

        fun build(): Connectivity {
            return Connectivity(this)
        }
    }

    companion object {
        internal val UNKNOWN_TYPE = -1
        internal val UNKNOWN_SUB_TYPE = -1

        fun create(): Connectivity {
            return Builder().build()
        }

        fun create(@NonNull context: Context): Connectivity {
            Preconditions.checkNotNull(context, "context == null")
            return create(context, getConnectivityManager(context))
        }

        protected fun create(@NonNull context: Context, manager: ConnectivityManager?): Connectivity {
            Preconditions.checkNotNull(context, "context == null")

            if (manager == null) {
                return create()
            }

            val networkInfo = manager.activeNetworkInfo
            return networkInfo?.let { create(it) } ?: create()
        }

        private fun create(networkInfo: NetworkInfo): Connectivity {
            return Builder().state(networkInfo.state)
                .detailedState(networkInfo.detailedState)
                .type(networkInfo.type)
                .subType(networkInfo.subtype)
                .available(networkInfo.isAvailable)
                .failover(networkInfo.isFailover)
                .roaming(networkInfo.isRoaming)
                .typeName(networkInfo.typeName)
                .subTypeName(networkInfo.subtypeName)
                .reason(networkInfo.reason)
                .extraInfo(networkInfo.extraInfo)
                .build()
        }

        private fun getConnectivityManager(context: Context): ConnectivityManager {
            val service = Context.CONNECTIVITY_SERVICE
            return context.getSystemService(service) as ConnectivityManager
        }
    }
}