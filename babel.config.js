const presets = [
    [
        "@babel/env",
        {
            targets: {
                edge: "13",
                ie: "11",
                firefox: "52",
                chrome: "69"

            },
            useBuiltIns: "usage"
        }
    ],
    "@babel/preset-react"
];

const plugins = [
    "@babel/plugin-transform-modules-commonjs"
];

module.exports = { presets, plugins };