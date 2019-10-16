package ru.victorpomidor.moneytransfer.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import javax.ws.rs.ext.ContextResolver
import javax.ws.rs.ext.Provider

/**
 * Hack for configuring ObjectMapper with Jersey
 */
@Provider
class ObjectMapperContextResolver : ContextResolver<ObjectMapper> {
    private val mapper: ObjectMapper = ObjectMapper()

    init {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.registerKotlinModule()
    }

    override fun getContext(type: Class<*>): ObjectMapper {
        return mapper
    }
}
