import { expect } from 'chai';
import { shallow } from 'enzyme';
import OffenderAlerts from './offenderAlerts';

describe('OffenderAlerts component', () => {

    let wrapper;
    let offenderConvictions;
    let registrations;

    beforeEach(() => {
        offenderConvictions = {
            convictions: [
                {
                    inBreach: false
                }
            ]
        };
        registrations = [];
    });

    describe('Breached conditions alert', () => {

        context('When the offender has breached active conviction conditions', () => {

            beforeEach(() => {
                offenderConvictions = offenderConvictions = {
                    convictions: [
                        {
                            active: true,
                            inBreach: false
                        },
                        {
                            active: true,
                            inBreach: true
                        }
                    ]
                };
                wrapper = shallow(<OffenderAlerts offenderConvictions={ offenderConvictions } registrations={ registrations }/>);
            });

            it('renders the breach alert', () => {
                expect(wrapper.find('.qa-alert-breach').exists()).equal(true);
            });

        });

        context('When the offender has breached inactive conviction conditions', () => {

            beforeEach(() => {
                offenderConvictions = offenderConvictions = {
                    convictions: [
                        {
                            active: true,
                            inBreach: false
                        },
                        {
                            active: false,
                            inBreach: true
                        }
                    ]
                };
                wrapper = shallow(<OffenderAlerts offenderConvictions={ offenderConvictions } registrations={ registrations }/>);
            });

            it('renders the breach alert', () => {
                expect(wrapper.find('.qa-alert-breach').exists()).equal(false);
            });

        });

        context('When the offender has NOT breached conviction conditions', () => {

            beforeEach(() => {
                offenderConvictions = offenderConvictions = {
                    convictions: [
                        {
                            active: true,
                            inBreach: false
                        },
                        {
                            active: true,
                            inBreach: false
                        }
                    ]
                };
                wrapper = shallow(<OffenderAlerts offenderConvictions={ offenderConvictions } registrations={ registrations }/>);
            });

            it('does not render the breach alert', () => {
                expect(wrapper.find('.qa-alert-breach').exists()).equal(false);
            });
        });

    });

    describe('RoSH alert', () => {

        context('When the offender has a very high RoSH registration', () => {

            beforeEach(() => {
                offenderConvictions = offenderConvictions = {};
                registrations = [
                    {
                        register: {
                            description: 'RoSH'
                        },
                        type: {
                            description: 'Very High RoSH'
                        },
                        riskColour: 'Red'
                    }
                ];
                wrapper = shallow(<OffenderAlerts offenderConvictions={ offenderConvictions } registrations={ registrations }/>);
            });

            it('renders the RoSH alert', () => {
                expect(wrapper.find('.qa-alert-rosh').exists()).to.equal(true);
            });
            it('renders the RoSH alert with the correct classname', () => {
                expect(wrapper.find('.qa-alert-rosh').hasClass('moj-risk-alert--high')).to.equal(true);
            });
            it('renders the RoSH alert with the correct text', () => {
                expect(wrapper.find('.qa-alert-rosh').text()).to.equal('very high risk of serious harm');
            });
        });

        context('When the offender has a high RoSH registration', () => {

            beforeEach(() => {
                offenderConvictions = offenderConvictions = {};
                registrations = [
                    {
                        register: {
                            description: 'RoSH'
                        },
                        type: {
                            description: 'High RoSH'
                        },
                        riskColour: 'Red'
                    }
                ];
                wrapper = shallow(<OffenderAlerts offenderConvictions={ offenderConvictions } registrations={ registrations }/>);
            });

            it('renders the RoSH alert', () => {
                expect(wrapper.find('.qa-alert-rosh').exists()).to.equal(true);
            });
            it('renders the RoSH alert with the correct classname', () => {
                expect(wrapper.find('.qa-alert-rosh').hasClass('moj-risk-alert--high')).to.equal(true);
            });
            it('renders the RoSH alert with the correct text', () => {
                expect(wrapper.find('.qa-alert-rosh').text()).to.equal('high risk of serious harm');
            });
        });

        context('When the offender has a medium RoSH registration', () => {

            beforeEach(() => {
                offenderConvictions = offenderConvictions = {};
                registrations = [
                    {
                        register: {
                            description: 'RoSH'
                        },
                        type: {
                            description: 'Medium RoSH'
                        },
                        riskColour: 'Amber'
                    }
                ];
                wrapper = shallow(<OffenderAlerts offenderConvictions={ offenderConvictions } registrations={ registrations }/>);
            });

            it('renders the RoSH alert', () => {
                expect(wrapper.find('.qa-alert-rosh').exists()).to.equal(true);
            });
            it('renders the RoSH alert with the correct classname', () => {
                expect(wrapper.find('.qa-alert-rosh').hasClass('moj-risk-alert--medium')).to.equal(true);
            });
            it('renders the RoSH alert with the correct text', () => {
                expect(wrapper.find('.qa-alert-rosh').text()).to.equal('medium risk of serious harm');
            });
        });

        context('When the offender has a low RoSH registration', () => {

            beforeEach(() => {
                offenderConvictions = offenderConvictions = {};
                registrations = [
                    {
                        register: {
                            description: 'RoSH'
                        },
                        type: {
                            description: 'Low RoSH'
                        },
                        riskColour: 'Green'
                    }
                ];
                wrapper = shallow(<OffenderAlerts offenderConvictions={ offenderConvictions } registrations={ registrations }/>);
            });

            it('renders the RoSH alert', () => {
                expect(wrapper.find('.qa-alert-rosh').exists()).to.equal(true);
            });
            it('renders the RoSH alert with the correct classname', () => {
                expect(wrapper.find('.qa-alert-rosh').hasClass('moj-risk-alert--low')).to.equal(true);
            });
            it('renders the RoSH alert with the correct text', () => {
                expect(wrapper.find('.qa-alert-rosh').text()).to.equal('low risk of serious harm');
            });
        });

        context('When the offender has NO RoSH registration', () => {

            beforeEach(() => {
                offenderConvictions = offenderConvictions = {};
                registrations = [];
                wrapper = shallow(<OffenderAlerts offenderConvictions={ offenderConvictions } registrations={ registrations }/>);
            });

            it('does not render the RoSH alert', () => {
                expect(wrapper.find('.qa-alert-rosh').exists()).to.equal(false);
            });
        });
    })
});