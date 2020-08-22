import pkg from '../package.json'
import { updatePluginXml, updateGradleProperties } from './lib/util';

updatePluginXml(pkg);
updateGradleProperties(pkg);
