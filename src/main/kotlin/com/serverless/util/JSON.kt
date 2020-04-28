package com.serverless.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

// TODO: Is Json thread safe?
val JSON = Json(JsonConfiguration.Stable)
