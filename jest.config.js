module.exports = {
	clearMocks: true,
	moduleFileExtensions: ['ts', 'js'],
	testEnvironment: 'node',
	//testMatch: ['**/*.test.ts', '**/*.spec.ts'],
	testRegex: ['\\.(tests?|spec)\\.(ts|tsx)$'],
	//testRunner: 'jest-circus/runner',
	setupFilesAfterEnv: [
		//"jest-chain",
		//"jest-extended",
		//"jest-extended-extra",
		//"jest-num-close-with",
	],
	transform: {
		'^.+\\.ts$': 'ts-jest',
	},
	verbose: true,
	/**
	 * if didn't set `coverageProvider` to `v8`
	 * with `collectCoverage` `true`, nodejs debug point maybe will fail
	 */
	coverageProvider: 'v8',
	collectCoverage: false,
}
