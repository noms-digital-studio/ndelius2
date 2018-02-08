import GovUkPhaseBanner  from './govukPhaseBanner'
import {expect} from 'chai';
import {shallow} from 'enzyme';

describe('GovUkPhaseBanner component', () => {
    let banner
    context('Full version rendered', () => {

        beforeEach(() => {
            banner = shallow(<GovUkPhaseBanner/>)
        })

        it('contains a LegacySearchLink', () => {
            expect(banner.find('Connect(LegacySearchLink)').exists()).to.be.true
        })

        it('contains a Feedback link', () => {
            expect(banner.find('Link').exists()).to.be.true
        })

    })
    context('Basic version rendered', () => {

        beforeEach(() => {
            banner = shallow(<GovUkPhaseBanner basicVersion={true}/>)
        })

        it('does not contain a LegacySearchLink', () => {
            expect(banner.find('Connect(LegacySearchLink)').exists()).to.be.false
        })

        it('does not contain a Feedback link', () => {
            expect(banner.find('Link').exists()).to.be.false
        })

    })
})

