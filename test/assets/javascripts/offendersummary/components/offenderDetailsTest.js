import { expect } from 'chai';
import { shallow } from 'enzyme';
import OffenderDetails from './offenderDetails';

describe('Offender Details component', () => {

    let wrapper;

    // CONTACT DETAILS

    describe('Main address section', () => {

        context('when all contact details are recorded', () => {

            const contactDetails = {
                addresses: [{}],
                emailAddresses: ['user@host.com'],
                phoneNumbers: [
                    {
                        number: '01753862474',
                        type: 'TELEPHONE'
                    },
                    {
                        number: '07777123456',
                        type: 'MOBILE'
                    }
                ]
            };

            beforeEach(() => {
                wrapper = shallow(<OffenderDetails contactDetails={ contactDetails }/>);
            });

            it('contains telephone number', () => {
                expect(wrapper.text()).to.contain('01753862474');
            });
            it('contains email address', () => {
                expect(wrapper.text()).to.contain('user@host.com');
            });
            it('contains mobile number', () => {
                expect(wrapper.text()).to.contain('07777123456');
            });
        });

        context('when no email address is recorded', () => {

            const contactDetails = {
                addresses: [{}],
                emailAddresses: [],
                phoneNumbers: [
                    {
                        number: '01753862474',
                        type: 'TELEPHONE'
                    },
                    {
                        number: '07777123456',
                        type: 'MOBILE'
                    }
                ]
            };

            beforeEach(() => {
                wrapper = shallow(<OffenderDetails contactDetails={ contactDetails }/>);
            });

            it('contains telephone number', () => {
                expect(wrapper.text()).to.contain('01753862474');
            });
            it('does not contain email address', () => {
                expect(wrapper.text()).to.contain('Unknown');
            });
            it('contains mobile number', () => {
                expect(wrapper.text()).to.contain('07777123456');
            });
        });

        context('when no telephone number is recorded', () => {

            const contactDetails = {
                addresses: [{}],
                emailAddresses: ['user@host.com'],
                phoneNumbers: [
                    {
                        number: '07777123456',
                        type: 'MOBILE'
                    }
                ]
            };

            beforeEach(() => {
                wrapper = shallow(<OffenderDetails contactDetails={ contactDetails }/>);
            });

            it('contains telephone number', () => {
                expect(wrapper.text()).to.contain('Unknown');
            });
            it('does not contain email address', () => {
                expect(wrapper.text()).to.contain('user@host.com');
            });
            it('contains mobile number', () => {
                expect(wrapper.text()).to.contain('07777123456');
            });
        });

        context('when no mobile number is recorded', () => {

            const contactDetails = {
                addresses: [{}],
                emailAddresses: ['user@host.com'],
                phoneNumbers: [
                    {
                        number: '01753862474',
                        type: 'TELEPHONE'
                    }
                ]
            };

            beforeEach(() => {
                wrapper = shallow(<OffenderDetails contactDetails={ contactDetails }/>);
            });

            it('contains telephone number', () => {
                expect(wrapper.text()).to.contain('01753862474');
            });
            it('does not contain email address', () => {
                expect(wrapper.text()).to.contain('user@host.com');
            });
            it('contains mobile number', () => {
                expect(wrapper.text()).to.contain('Unknown');
            });
        });
    });

    // MAIN ADDRESS

    describe('Main address section', () => {

        context('when a main address is recorded', () => {

            const contactDetails = {
                addresses: [{
                    addressNumber: '5',
                    buildingName: 'Sea View',
                    county: 'Yorkshire',
                    district: 'Nether Edge',
                    from: '2018-06-22',
                    noFixedAbode: false,
                    notes: '',
                    postcode: 'S10 1EQ',
                    status: { code: 'M', description: 'Main' },
                    streetName: 'High Street',
                    telephoneNumber: '',
                    town: 'Sheffield'
                }],
                emailAddresses: [],
                phoneNumbers: []
            };

            beforeEach(() => {
                wrapper = shallow(<OffenderDetails contactDetails={ contactDetails }/>);
            });

            it('contains address number and building name', () => {
                expect(wrapper.text()).to.contain('5 Sea View');
            });
            it('contains street name', () => {
                expect(wrapper.text()).to.contain('High Street, Nether Edge');
            });
            it('contains district and town', () => {
                expect(wrapper.text()).to.contain('Sheffield');
            });
            it('contains postcode', () => {
                expect(wrapper.text()).to.contain('S10 1EQ');
            });

            it('does not contain no fixed abode line', () => {
                expect(wrapper.text()).not.to.contain('No fixed abode');
            });
            it('does not contain no main address line', () => {
                expect(wrapper.text()).not.to.contain('No main address');
            });
        });

        context('when a main address is recorded but has no fixed abode', () => {

            const contactDetails = {
                addresses: [{
                    noFixedAbode: true,
                    status: { code: 'M', description: 'Main' }
                }],
                emailAddresses: [],
                phoneNumbers: []
            };

            beforeEach(() => {
                wrapper = shallow(<OffenderDetails contactDetails={ contactDetails }/>);
            });

            it('contains no fixed abode', () => {
                expect(wrapper.text()).to.contain('No fixed abode');
            });
        });

        context('when no main address is recorded', () => {

            const contactDetails = {
                addresses: [{
                    noFixedAbode: true
                }],
                emailAddresses: [],
                phoneNumbers: []
            };

            beforeEach(() => {
                wrapper = shallow(<OffenderDetails contactDetails={ contactDetails }/>);
            });

            it('contains no main address', () => {
                expect(wrapper.text()).to.contain('No main address');
            });
        });
    });
});
