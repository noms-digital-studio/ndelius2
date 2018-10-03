import GovUkPhaseBanner from './govukPhaseBanner';
import { expect } from 'chai';
import { shallow } from 'enzyme';

describe('GovUkPhaseBanner component', () => {
    let banner;
    context('Banner rendered', () => {

        beforeEach(() => {
            banner = shallow(<GovUkPhaseBanner/>);
        });

        it('contains a LegacySearchLink', () => {
            expect(banner.find('Connect(LegacySearchLink)').exists()).to.be.true;
        });

        it('contains a FeedbackLink', () => {
            expect(banner.find('FeedbackLink').exists()).to.be.true;
        });

    });
});
