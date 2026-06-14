package com.example.ui

import kotlinx.coroutines.flow.MutableSharedFlow

val _globalToastChannel = MutableSharedFlow<Pair<String, String>>(extraBufferCapacity = 8)
