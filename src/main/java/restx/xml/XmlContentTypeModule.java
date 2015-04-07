package restx.xml;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Optional;
import restx.entity.EntityResponseWriter;
import restx.entity.EntityResponseWriterFactory;
import restx.factory.Module;
import restx.factory.Provides;

import javax.inject.Named;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Module
public class XmlContentTypeModule {

    public static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile("^((?:text/xml)|(?:application/xml))(;.*)?$");

    @Provides
    public EntityResponseWriterFactory xmlEntityResponseWriterFactory(
            @Named(XmlObjectMapperFactory.WRITER_NAME) final ObjectWriter objectWriter) {
        return new EntityResponseWriterFactory() {
            @Override
            public <T> Optional<? extends EntityResponseWriter<T>> mayBuildFor(Type valueType, String contentType) {
                Matcher contentTypematcher = CONTENT_TYPE_PATTERN.matcher(contentType);
                if (!contentTypematcher.find() || contentTypematcher.groupCount() < 1) {
                    return Optional.absent();
                }

                return Optional.of(XmlEntityResponseWriter.<T>using(contentTypematcher.group(1), valueType, objectWriter));
            }
        };
    }
}
