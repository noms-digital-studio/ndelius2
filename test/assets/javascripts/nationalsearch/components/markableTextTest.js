import MarkableText, {matchesHighlightedField, matchesAnyHighlightedField}  from './markableText'
import {expect} from 'chai';
import { shallow } from 'enzyme';

describe('MarkableText component', () => {
    describe('matchesHighlightedField', () => {
        it('returns true when field name matches a highlighted field', () => {
            expect(matchesHighlightedField({myField: ['text']}, 'myField')).to.be.true
        })

        it('returns false when field name does not match a highlighted field', () => {
            expect(matchesHighlightedField({myField: ['text']}, 'notMyField')).to.be.false
        })

    })
    describe('matchesAnyHighlightedField', () => {
        it('returns true when one of the field names matches a highlighted field', () => {
            expect(matchesAnyHighlightedField({myField: ['text']}, ['myField', 'myOtherField'])).to.be.true
        })

        it('returns false when no field names match a highlighted field', () => {
            expect(matchesAnyHighlightedField({myField: ['text']}, ['notMyField', 'notyMyOtherField'])).to.be.false
        })

    })
    context('when highlight field matches', () => {

        context('no matches found', () => {
            it('no highlighted element rendered', () => {
                const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'donkey'} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: 'Bobby Beans', highlight: false})).to.have.length(1)
            })
        })
        context('one match found', () => {
            it('one highlighted element rendered', () => {
                const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'beans'} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: 'Beans', highlight: true})).to.have.length(1)
                expect(markedText.find({text: 'Bobby ', highlight: false})).to.have.length(1)
            })
        })
        context('multiple matches found with multiple search words', () => {
            it('multiple highlighted elements rendered', () => {
                const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'bobby beans'} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: 'Beans', highlight: true})).to.have.length(1)
                expect(markedText.find({text: 'Bobby', highlight: true})).to.have.length(1)
                expect(markedText.find({text: ' ', highlight: false})).to.have.length(1)
            })
            it('multiple highlighted elements rendered when single char words allowed for tiny search terms', () => {
                const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'b'} highlight={{someField: ['text']}} highlightFieldName='someField' allowSingleCharacter/>)
                expect(markedText.find({text: 'B', highlight: true})).to.have.length(2)
                expect(markedText.find({text: 'bb', highlight: true})).to.have.length(1)
            })
            it('multiple highlighted elements not rendered for tiny search terms by default', () => {
                const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'b'} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: 'B', highlight: true})).to.have.length(0)
                expect(markedText.find({text: 'bb', highlight: true})).to.have.length(0)
            })
        })
        context('overlapping multiple matches found with multiple search words', () => {
            it('multiple highlighted elements rendered but not the overlapped one', () => {
                const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'bobby beans obb'} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find('Text')).to.have.length(3)
                expect(markedText.find({text: 'Beans', highlight: true})).to.have.length(1)
                expect(markedText.find({text: 'Bobby', highlight: true})).to.have.length(1)
                expect(markedText.find({text: ' ', highlight: false})).to.have.length(1)
            })
        })
    })
    context('when highlight field does not match', () => {

        context('no matches found', () => {
            it('no highlighted element rendered', () => {
                const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'donkey'} highlight={{someField: ['text']}} highlightFieldName='someOtherField'/>)
                expect(markedText.find('Text').exists()).to.be.false
            })
        })
        context('one match found', () => {
            it('no highlighted element rendered', () => {
                const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'beans'} highlight={{someField: ['text']}} highlightFieldName='someOtherField'/>)
                expect(markedText.find('Text').exists()).to.be.false
            })
        })
        context('multiple matches found with multiple search words', () => {
            it('no highlighted element rendered', () => {
                const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'bobby beans'} highlight={{someField: ['text']}} highlightFieldName='someOtherField'/>)
                expect(markedText.find('Text').exists()).to.be.false
            })
            it('no highlighted element rendered', () => {
                const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'b'} highlight={{someField: ['text']}} highlightFieldName='someOtherField'/>)
                expect(markedText.find('Text').exists()).to.be.false
            })
        })
        context('overlapping multiple matches found with multiple search words', () => {
            it('no highlighted element rendered', () => {
                const markedText = shallow(<MarkableText text={'Bobby Beans'} searchTerm={'bobby beans obb'} highlight={{someField: ['text']}} highlightFieldName='someOtherField'/>)
                expect(markedText.find('Text').exists()).to.be.false
            })
        })
    })
    describe('date handling', () => {
        context('date matches in same format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'1975-07-13 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/MMMM/dd format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'1975/July/13 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/MMMM/d format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'1975/July/3 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/MMM/dd format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'1975/Jul/13 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/MMM/d format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'1975/Jul/3 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/MM/dd format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'1975/07/13 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/MM/d format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'1975/07/3 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/M/dd format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'1975/7/13 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/M/d format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'1975/7/3 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd/MMMM/yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13/July/1975 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in d/MMMM/yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'3/July/1975 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd/MMM/yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13/Jul/1975 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in d/MMM/yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'3/Jul/1975 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd/MM/yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13/07/1975 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in d/MM/yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'3/07/1975 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd/MM/yy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13/07/75 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd-MMMM-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13-July-1975 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in d-MMMM-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'3-July-1975 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd-MMM-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13-Jul-1975 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in d-MMM-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'3-Jul-1975 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd-MM-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13-07-1975 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in d-MM-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'3-07-1975 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd-M-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13-7-1975 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in d-M-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'3-7-1975 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd-MM-yy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13-07-75 beans'} isDate={true} highlight={{someField: ['text']}} highlightFieldName='someField'/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
    })
})


