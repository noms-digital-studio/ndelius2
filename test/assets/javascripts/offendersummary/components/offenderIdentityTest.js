import OffenderIdentity  from './offenderIdentity'
import {expect} from 'chai';
import {shallow} from 'enzyme';

describe('Offender Identity component', () => {
    let wrapper
    const offenderDetails = {
        firstName: 'John',
        surname: 'Smith',
        dateOfBirth: '1998-06-22',
        otherIds: {
            crn: 'X123456'
        }
    }


    describe('detail section', () => {

        beforeEach(() => {
            wrapper = shallow(<OffenderIdentity offenderDetails={offenderDetails}/>)
        })

        it('contains name', () => {
            expect(wrapper.text()).to.contain("Smith, John");
        })
        it('contains crn', () => {
            expect(wrapper.text()).to.contain("X123456");
        })
        it('contains formatted date of birth', () => {
            expect(wrapper.text()).to.contain("22/06/1998");
        })
    })
    describe('image section', () => {
        context('when no image reference', () => {
            beforeEach(() => {
                wrapper = shallow(<OffenderIdentity offenderDetails={offenderDetails}/>)
            })
            it('renders image tag with blank image url', () => {
                expect(wrapper.find({
                    src: 'assets/images/NoPhoto@2x.png'
                }).exists()).to.be.true
            })
        })
        context('when has an image reference', () => {
            beforeEach(() => {
                const details = {...offenderDetails, oneTimeNomisRef: 'IMAGEREF'}
                wrapper = shallow(<OffenderIdentity offenderDetails={details}/>)
            })
            it('renders image tag with noms image url', () => {
                expect(wrapper.find({
                    src: `offender/oneTimeNomisRef/IMAGEREF/image`
                }).exists()).to.be.true
            })
        })
    })
})

