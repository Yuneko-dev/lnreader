import { TurboModule, TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  downloadApk: (
    url: string,
    title: string,
    description: string,
  ) => Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('NativeDownloadManager');
