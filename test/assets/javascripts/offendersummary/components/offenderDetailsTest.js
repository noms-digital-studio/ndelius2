import { expect } from 'chai';
import { shallow } from 'enzyme';
import OffenderDetails from './offenderDetails';

describe('Offender Details component', () => {

    let wrapper;

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
                emailAddresses: []
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
                emailAddresses: []
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
                emailAddresses: []
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
