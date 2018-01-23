import MarkableText  from './markableText'
import {expect} from 'chai';
import { shallow } from 'enzyme';

describe('MarkableText component', () => {
    context('no matches found', () => {
        it('no highlighted element rendered', () => {
            const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'donkey'}/>)
            expect(markedText.find({text: 'Bobby Beans', highlight: false})).to.have.length(1)
        })
    })
    context('one match found', () => {
        it('one highlighted element rendered', () => {
            const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'beans'}/>)
            expect(markedText.find({text: 'Beans', highlight: true})).to.have.length(1)
            expect(markedText.find({text: 'Bobby ', highlight: false})).to.have.length(1)
        })
    })
    context('multiple matches found with multiple search words', () => {
        it('multiple highlighted elements rendered', () => {
            const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'bobby beans'}/>)
            expect(markedText.find({text: 'Beans', highlight: true})).to.have.length(1)
            expect(markedText.find({text: 'Bobby', highlight: true})).to.have.length(1)
            expect(markedText.find({text: ' ', highlight: false})).to.have.length(1)
        })
        it('multiple highlighted elements rendered for tiny search terms', () => {
            const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'b'}/>)
            expect(markedText.find({text: 'B', highlight: true})).to.have.length(2)
            expect(markedText.find({text: 'bb', highlight: true})).to.have.length(1)
        })
    })
    context('overlapping multiple matches found with multiple search words', () => {
        it('multiple highlighted elements rendered but not the overlapped one', () => {
            const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'bobby beans obb'}/>)
            expect(markedText.find('Text')).to.have.length(3)
            expect(markedText.find({text: 'Beans', highlight: true})).to.have.length(1)
            expect(markedText.find({text: 'Bobby', highlight: true})).to.have.length(1)
            expect(markedText.find({text: ' ', highlight: false})).to.have.length(1)
        })
    })
})


