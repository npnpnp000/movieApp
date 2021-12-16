package com.movieapp.Util

import com.bumptech.glide.load.Key
import java.nio.ByteBuffer
import java.security.MessageDigest

class IntegerVersionSignature(var currentVersion: Int): Key {

    override fun equals(o: Any?): Boolean {
        if (o is IntegerVersionSignature) {
             val other :IntegerVersionSignature = o
            return currentVersion == other.currentVersion
        }
        return false
    }

    override fun hashCode(): Int {
        return currentVersion
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ByteBuffer.allocate(Integer.SIZE).putInt(currentVersion).array())
    }


}