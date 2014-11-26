package restx.xml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import restx.AppSettings;
import restx.RestxContext;
import restx.factory.Factory;
import restx.factory.Module;
import restx.factory.Name;
import restx.factory.Provides;
import restx.jackson.Views;

import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.Set;

@Module
public class XmlObjectMapperFactory {

    public static final String MAPPER_NAME = "XmlObjectMapper";
    public static final Name<ObjectMapper> NAME = Name.of(ObjectMapper.class, MAPPER_NAME);
    public static final String WRITER_NAME = "XmlObjectWriter";
    public static final String READER_NAME = "XmlObjectReader";

    @Provides @Named(MAPPER_NAME)
    public ObjectMapper mapper(final Factory factory) {
        ObjectMapper mapper = new XmlMapper()
                .enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"))
                .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .registerModule(new JodaModule())
                .registerModule(new GuavaModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.EAGER_DESERIALIZER_FETCH)
                .disable(SerializationFeature.EAGER_SERIALIZER_FETCH);

        Set<com.fasterxml.jackson.databind.Module> modules = factory.getComponents(com.fasterxml.jackson.databind.Module.class);
        mapper.registerModules(modules);

        mapper.setHandlerInstantiator(new HandlerInstantiator() {
            @Override
            public JsonDeserializer<?> deserializerInstance(DeserializationConfig config, Annotated annotated, Class<?> deserClass) {
                return (JsonDeserializer<?>) factory.queryByClass(deserClass).optional().findOneAsComponent().orNull();
            }

            @Override
            public KeyDeserializer keyDeserializerInstance(DeserializationConfig config, Annotated annotated, Class<?> keyDeserClass) {
                return (KeyDeserializer) factory.queryByClass(keyDeserClass).optional().findOneAsComponent().orNull();
            }

            @Override
            public JsonSerializer<?> serializerInstance(SerializationConfig config, Annotated annotated, Class<?> serClass) {
                return (JsonSerializer<?>) factory.queryByClass(serClass).optional().findOneAsComponent().orNull();
            }

            @Override
            public TypeResolverBuilder<?> typeResolverBuilderInstance(MapperConfig<?> config, Annotated annotated, Class<?> builderClass) {
                return (TypeResolverBuilder<?>) factory.queryByClass(builderClass).optional().findOneAsComponent().orNull();
            }

            @Override
            public TypeIdResolver typeIdResolverInstance(MapperConfig<?> config, Annotated annotated, Class<?> resolverClass) {
                return (TypeIdResolver) factory.queryByClass(resolverClass).optional().findOneAsComponent().orNull();
            }
        });

        return mapper;
    }

    @Provides @Named(WRITER_NAME)
    public ObjectWriter objectWriter(@Named(XmlObjectMapperFactory.MAPPER_NAME) final ObjectMapper mapper,
                                     AppSettings appSettings) {
        ObjectWriter objectWriter = RestxContext.Modes.PROD.equals(appSettings.mode())
                                    ? mapper.writer() : mapper.writerWithDefaultPrettyPrinter();
        return objectWriter.withView(Views.Transient.class);
    }

    @Provides @Named(READER_NAME)
    public ObjectReader objectReader(@Named(XmlObjectMapperFactory.MAPPER_NAME) final ObjectMapper mapper) {
        return mapper.readerWithView(Views.Transient.class);
    }
}
