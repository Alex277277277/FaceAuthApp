package com.auth.face.faceauth.base

interface OneShotDataWrapperConsumer<Data> {
    fun consume(data: Data)
}
