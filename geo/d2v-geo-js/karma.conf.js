module.exports = function (config) {
    config.set({
            frameworks: ['qunit', 'browserify'],
            reporters: ['mocha'],
            files: [
                'build/classes/main/*.js',
                'build/classes/test/*.js',
                'build/node_modules/*.js'
            ],
            exclude: [],
            colors: true,
            autoWatch: false,
            browsers: [
                'PhantomJS'
                // , 'Chrome'
            ],
            captureTimeout: 50000,
            singleRun: true,
        // singleRun: false,
            reportSlowerThan: 500,

            preprocessors: {
                '**/*.js': ['browserify']
            }
        }
    )
};