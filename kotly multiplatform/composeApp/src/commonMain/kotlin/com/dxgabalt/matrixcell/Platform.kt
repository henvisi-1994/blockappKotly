package com.dxgabalt.matrixcell

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform