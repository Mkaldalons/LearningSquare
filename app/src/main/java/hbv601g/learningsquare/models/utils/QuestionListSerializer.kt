package hbv601g.learningsquare.models.utils

import hbv601g.learningsquare.models.QuestionModel
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

object QuestionListSerializer : KSerializer<List<QuestionModel>> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("QuestionList", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<QuestionModel> {
        val jsonString = decoder.decodeString()
        return Json.decodeFromString(ListSerializer(QuestionModel.serializer()), jsonString)
    }

    override fun serialize(encoder: Encoder, value: List<QuestionModel>) {
        val jsonString = Json.encodeToString(ListSerializer(QuestionModel.serializer()), value)
        encoder.encodeString(jsonString)
    }
}
