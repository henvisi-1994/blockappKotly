package com.dxgabalt.matrixcell
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DeviceManager() {

    fun blockDevice()
    fun unblockDevice()
    fun navigateToPayments()
    fun checkInternetConnection()
    fun callSupport()
    fun getInternetConnection():Boolean

}
