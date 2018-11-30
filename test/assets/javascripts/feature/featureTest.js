import feature from './feature'
import {expect} from 'chai';
import {stub} from 'sinon';

describe("feature", () => {
    let cookies = {}
    beforeEach(() => {
        cookies.get = stub()
    })

    context('when in global switch is "must"', () => {
        beforeEach(() => {
            global.window.featureMyCoolNewFeature = 'must'
            cookies.get.withArgs('featureMyCoolNewFeature').returns('false')
        })
        it('always return true', () => {
            expect(feature.isEnabled(cookies, 'myCoolNewFeature')).to.equal(true)
        });
    })

    context('when in global switch is "disallowed"', () => {
        beforeEach(() => {
            global.window.featureMyCoolNewFeature = 'disallowed'
            cookies.get.withArgs('featureMyCoolNewFeature').returns('true')
        })
        it('always return false', () => {
            expect(feature.isEnabled(cookies, 'myCoolNewFeature')).to.equal(false)
        });
    })

    context('when in global switch is "allowed"', () => {
        beforeEach(() => {
            global.window.featureMyCoolNewFeature = 'allowed'
        })

        context('when cookie is missing', () => {
            beforeEach(() => {
                cookies.get.withArgs('featureMyCoolNewFeature').returns(null)
            })
            it('will return false', () => {
                expect(feature.isEnabled(cookies, 'myCoolNewFeature')).to.equal(false)
            });
        })
        context('when cookie is false', () => {
            beforeEach(() => {
                cookies.get.withArgs('featureMyCoolNewFeature').returns('false')
            })
            it('will return false', () => {
                expect(feature.isEnabled(cookies, 'myCoolNewFeature')).to.equal(false)
            });
        })
        context('when cookie is true', () => {
            beforeEach(() => {
                cookies.get.withArgs('featureMyCoolNewFeature').returns('true')
            })
            it('will return true', () => {
                expect(feature.isEnabled(cookies, 'myCoolNewFeature')).to.equal(true)
            });
        })
    })

})
