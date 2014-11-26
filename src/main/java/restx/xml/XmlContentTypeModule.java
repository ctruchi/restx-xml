package restx.xml;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Optional;
import restx.entity.EntityResponseWriter;
import restx.entity.EntityResponseWriterFactory;
import restx.factory.Module;
import restx.factory.Provides;

import javax.inject.Named;
import java.lang.reflect.Type;

@Module
public class XmlContentTypeModule {

    @Provides
    public EntityResponseWriterFactory xmlEntityResponseWriterFactory(
            @Named(XmlObjectMapperFactory.WRITER_NAME) final ObjectWriter objectWriter) {
        return new EntityResponseWriterFactory() {
            @Override
            public <T> Optional<? extends EntityResponseWriter<T>> mayBuildFor(Type valueType, String contentType) {
                if (!contentType.startsWith("text/xml")) {
                    return Optional.absent();
                }

                return Optional.of(XmlEntityResponseWriter.<T>using(valueType, objectWriter));
            }
        };
    }
}
