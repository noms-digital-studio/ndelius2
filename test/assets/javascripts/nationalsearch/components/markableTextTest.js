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
    describe('date handling', () => {
        context('date matches in same format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'1975-07-13 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/MMMM/dd format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'1975/July/13 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/MMMM/d format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'1975/July/3 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/MMM/dd format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'1975/Jul/13 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/MMM/d format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'1975/Jul/3 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/MM/dd format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'1975/07/13 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/MM/d format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'1975/07/3 beans'} isDate={true}/>)
                console.log(markedText.debug())
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/M/dd format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'1975/7/13 beans'} isDate={true}/>)
                console.log(markedText.debug())
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in yyyy/M/d format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'1975/7/3 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd/MMMM/yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13/July/1975 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in d/MMMM/yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'3/July/1975 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd/MMM/yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13/Jul/1975 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in d/MMM/yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'3/Jul/1975 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd/MM/yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13/07/1975 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in d/MM/yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'3/07/1975 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd/MM/yy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13/07/75 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd-MMMM-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13-July-1975 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in d-MMMM-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'3-July-1975 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd-MMM-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13-Jul-1975 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in d-MMM-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'3-Jul-1975 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd-MM-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13-07-1975 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in d-MM-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'3-07-1975 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd-M-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13-7-1975 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in d-M-yyyy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-03'} searchTerm={'3-7-1975 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-03', highlight: true})).to.have.length(1)
            })
        })
        context('date matches in dd-MM-yy format', () => {
            it('date is highlighted', () => {
                const markedText = shallow(<MarkableText text={'1975-07-13'} searchTerm={'13-07-75 beans'} isDate={true}/>)
                expect(markedText.find({text: '1975-07-13', highlight: true})).to.have.length(1)
            })
        })
    })
})


