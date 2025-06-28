import com.github.dimitryivaniuta.foundation.config.Config;
import com.github.dimitryivaniuta.foundation.config.ConfigModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = ConfigModule.class)
interface TestComponent {
    Config config();
}