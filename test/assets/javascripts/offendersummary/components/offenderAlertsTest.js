import { expect } from 'chai';
import { shallow } from 'enzyme';
import OffenderAlerts from './offenderAlerts';

describe('OffenderAlerts component', () => {

    let wrapper;
    let offenderConvictions;

    beforeEach(() => {
        offenderConvictions = {
            convictions: [
                {
                    inBreach: false
                }
            ]
        };
    });

    describe('Offender Alerts', () => {

        context('When the offender has breached conviction conditions', () => {

            beforeEach(() => {

                offenderConvictions = offenderConvictions = {
                    convictions: [
                        {
                            inBreach: false
                        },
                        {
                            inBreach: true
                        }
                    ]
                };

                wrapper = shallow(<OffenderAlerts offenderConvictions={ offenderConvictions }/>);
            });

            it('renders the breach alert', () => {
                expect(wrapper.find('.qa-alert-breach').exists()).to.be.true;
            });

        });

        context('When the offender has NOT breached conviction conditions', () => {

            beforeEach(() => {

                offenderConvictions = offenderConvictions = {
                    convictions: [
                        {
                            inBreach: false
                        },
                        {
                            inBreach: false
                        }
                    ]
                };

                wrapper = shallow(<OffenderAlerts offenderConvictions={ offenderConvictions }/>);
            });

            it('does not render the breach alert', () => {
                expect(wrapper.find('.qa-alert-breach').exists()).to.be.false;
            });
        });
    })
});