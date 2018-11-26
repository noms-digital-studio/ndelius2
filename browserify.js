console.log("Running browserify with babelify for " + process.argv[2] + " => " + process.argv[3])

var source = process.argv[2]
var bundle = process.argv[3]
var fs = require("fs")
var browserify = require('browserify');

browserify(source)
    .transform("babelify", {
        global: false
    })
    .bundle()
    .pipe(fs.createWriteStream(bundle));
