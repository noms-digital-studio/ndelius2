require("@babel/register")

React = require('react')

let enzyme = require('enzyme');
let Adapter = require('enzyme-adapter-react-16');

enzyme.configure({ adapter: new Adapter() });

_ = {
    debounce: (func) => func
}

global._ = _
require('chai').use(require('sinon-chai')).use(require('chai-shallow-deep-equal'));

global.parent = {}
global.top = {}
global.self = {}
global.window = {}
