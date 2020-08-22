import { __root } from "./lib/util";
import pkg from '../package.json'
import { copyFileSync, copySync } from 'fs-extra';
import { join, relative } from "path";

let file = join(__root, 'build', 'libs', `idea-run-typescript-${pkg.version}.jar`);
let target = join(__root, 'releases', 'idea-run-typescript.jar');

let label = [`copy`, relative(__root, file), `=>`, relative(__root, target)].join(' ');

console.time(label);

copySync(file, target, {
	preserveTimestamps: true,
	overwrite: true,
	dereference: true,
});

console.timeEnd(label);
