import { __root, console } from "./lib/util";
import pkg from '../package.json'
import { copyFileSync, copySync } from 'fs-extra';
import { join, relative } from "path";

let file = join(__root, 'build', 'libs', `idea-run-typescript-${pkg.version}.jar`);
let target = join(__root, 'releases', 'idea-run-typescript.jar');

let file2 = join(__root, 'build', 'distributions', `idea-run-typescript-${pkg.version}.zip`);
let target2 = join(__root, 'releases', 'idea-run-typescript.zip');

let label = [`copy`, relative(__root, file), `=>`, relative(__root, target)].join(' ');
let label2 = [`copy`, relative(__root, file2), `=>`, relative(__root, target2)].join(' ');

copySync(file, target, {
	preserveTimestamps: true,
	overwrite: true,
	dereference: true,
});

console.success(label);

copySync(file2, target2, {
	preserveTimestamps: true,
	overwrite: true,
	dereference: true,
});

console.success(label2);
