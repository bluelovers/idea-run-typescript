import pkg from '../package.json'
import { writeFileSync } from "fs";
import { join } from 'path';
import { inspect } from "util";
import { updateGradleProperties, updatePluginXml, __root } from './lib/util';

const date = new Date();

const prefix = [
	date.getFullYear(),
	(date.getMonth() + 1).toString().padStart(2, '0'),
	date.getDate().toString().padStart(2, '0'),
].join('.') + '-';

let idx = 0;

if (pkg.version.startsWith(prefix))
{
	idx = +(pkg.version.match(/\-(\d+)$/)[1]) + 1
}

const version = prefix + idx;

console.log(pkg.version, `=>`, version);

updatePackageJson();
updatePluginXml(pkg);
updateGradleProperties(pkg);

function updatePackageJson()
{
	pkg.version = version;

	console.log(`update`, `package.json`);
	writeFileSync(join(__root, 'package.json'), JSON.stringify(pkg, null, 2));
}

