const path = require('path');
const merge = require('webpack-merge');
// logging :
//const winston = require('winston');
const fs = require('fs')

const TARGET = process.env.npm_lifecycle_event;
const PATHS = {
    source: path.join(__dirname, 'app'),
    output: path.join(__dirname, '../../../target/classes/static')
};

/*
const logger = winston.createLogger({
  level: 'info',
  format: winston.format.json(),
  transports: [
    //new winston.transports.File({ filename: 'error.log', level: 'error' }),
    new winston.transports.File({ filename: 'webpack.log' })
  ]
});
*/
const logFileWriter = fs.openSync('webpack.log', 'a+'); // append, not 'w+'

const common = {
    entry: [
        PATHS.source
    ],
    output: {
        path: PATHS.output,
        publicPath: '',
        filename: 'bundle.js'
    },
    module: {
        loaders: [{
            exclude: /node_modules/,
            loader: 'babel'
        },
        {
            test: /\.css$/,
            loader: 'style!css'
        },
        {
            test: /\.scss$/,
            loaders: [ 'style', 'css', 'sass' ]
        }    
        ]
    },
    resolve: {
        extensions: ['', '.js', '.jsx']
    }
};

if (TARGET === 'start' || !TARGET) {
    module.exports = merge(common, {
        devServer: {
            port: 9090,
            proxy: {
                '/': {
                    target: 'http://localhost:8080',
                    secure: false,
                    prependPath: false
                }
            },
            publicPath: 'http://localhost:9090/',
            historyApiFallback: true
        },
        devtool: 'source-map',
    plugins: [
        // NO STILL ONLY AT STARTUP else compilation errors are not displayed https://github.com/webpack/webpack/issues/708
        function()
    {
        this.plugin("done", function(stats)
        {
            if (stats.compilation.errors && stats.compilation.errors.length)
            {
                //logger.log({ level: 'info', message : JSON.stringify(stats.compilation.errors, null, '\t') });
                fs.writeSync(logFileWriter, 'youhou'/*JSON.stringify(stats.compilation.errors, null, '\t')*/ + '\n', null, 'utf-8');
                ///process.exit(1);
            }
        });
    }
    ]
    });
}

if (TARGET === 'build') {
    module.exports = merge(common, {});
}

