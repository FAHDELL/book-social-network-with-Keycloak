import { APP_INITIALIZER, ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { httpTokenInterceptor } from './services/interceptor/http-token.interceptor';
import { ApiModule } from './services/api.module';
import { KeycloakService } from './services/keycloak/keycloak.service';

export function kcFactory(kcService: KeycloakService) {
  return () => kcService.init();
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([httpTokenInterceptor])),
    // 🔹 Ajouter ApiModule avec rootUrl
    /*
    importProvidersFrom(
      ApiModule.forRoot({
        rootUrl: 'http://192.168.0.227:8088',
      }),
    ),*/

    {
      provide: APP_INITIALIZER,
      useFactory: kcFactory,
      deps: [KeycloakService],
      multi: true
    }
  ],
};
