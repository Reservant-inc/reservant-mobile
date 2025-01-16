package reservant_mobile.data.models.dtos

import kotlinx.serialization.Serializable

@Serializable
enum class MessageThreadType{
    Normal,
    Event,
    Report
}