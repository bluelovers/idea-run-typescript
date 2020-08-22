import { join, relative } from "path";
import DotProperties from 'dot-properties-loader';
import { readFileSync, writeFileSync } from "fs";
import { convert } from 'xmlbuilder2';
import pluginPkg from '../../package.json'
import { inspect } from "util";

export const __root = join(__dirname, '../../');

inspect.defaultOptions.colors = true;

export function updatePluginXml(pkg: typeof pluginPkg)
{
	let file = join(__root, 'resources/META-INF/plugin.xml');

	let source = readFileSync(file, 'utf8');

	const obj = convert(source, { format: "object" });

	let root = obj['idea-plugin'];

	root.version = pkg.version;
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

export function updateGradleProperties(pkg: typeof pluginPkg)
{
	let file = join(__root, 'gradle.properties');
	let dp = new DotProperties({
		file,
	});

	dp.set('version', pkg.version);
	dp.set('pluginVersion', pkg.version);

	console.log(`update`, relative(__root, file));

	dp.save();
}
