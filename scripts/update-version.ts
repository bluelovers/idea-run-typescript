import pkg from '../package.json'
import {readdirSync, readFile, readFileSync, writeFileSync} from "fs";
import {join,relative} from 'path';
import {create,convert} from "xmlbuilder2";
import {inspect} from "util";
import DotProperties from 'dot-properties-loader';

inspect.defaultOptions.colors = true;

const __root = join(__dirname, '../');

const date = new Date();

const prefix = [
	date.getFullYear(),
	(date.getMonth() + 1).toString().padStart(2, '0'),
	date.getDate().toString().padStart(2, '0'),
	'',
].join('.');

let idx = 0;

if (pkg.version.startsWith(prefix)) {
	idx = +(pkg.version.match(/\.(\d+)$/)[1]) + 1
}

const version = prefix + idx;

console.log(pkg.version, `=>`, version);

updatePackageJson();
updatePluginXml();
updateGradleProperties();

function updatePackageJson() {
	pkg.version = version;

	console.log(`update`, `package.json`);
	writeFileSync(join(__root, 'package.json'), JSON.stringify(pkg, null, 2));
}

function updatePluginXml() {

	let file = join(__root, 'resources/META-INF/plugin.xml');

	let source = readFileSync(file, 'utf8');

	const obj = convert(source, { format: "object" });

	let root = obj['idea-plugin'];

	root.version = version;
	root['idea-version']['@since-build'] = pkg.engines["idea-version"];
	root.vendor['#'] = pkg.author;

	const xml = convert(obj, {
		format: "xml",
		prettyPrint: true,
	})
		.replace(/^\<\?xml version="1\.0"\?\>\s*/, '')
	;

	console.log(`update`, `resources/META-INF/plugin.xml`);
	writeFileSync(file, xml);
}

function updateGradleProperties() {
	let file = join(__root, 'gradle.properties');
	let dp = new DotProperties({
		file
	});

	dp.set('version', version);
	dp.set('pluginVersion', version);

	console.log(`update`, relative(__root, file));

	dp.save();
}
