import Suggestions from './suggestions'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('Suggestions component', () => {
    let search;
    beforeEach(() => {
        search = stub()
    })
    describe(('rendering of suggestions'), () => {

        context('no suggestions have been found', () => {
            it('no text rendered', () => {
                const suggestions = shallow(<Suggestions suggestions={[]} search={search} searchTerm={'Gery Smyth'}/>)
                expect(suggestions.text()).to.equal('')
            })
        })
        context('some suggestions are found', () => {
            let suggestions
            beforeEach(() => {
                const suggestionsData =                         [
                    {
                        text: "gery",
                        options: [
                            {
                                "text": "gary",
                                "score": 0.8
                            }
                        ]
                    },
                    {
                        text: "smyth",
                        options: [
                            {
                                "text": "smith",
                                "score": 0.8
                            }
                        ]
                    }
                ]
                suggestions = shallow(<Suggestions suggestions={suggestionsData} search={search} searchTerm={'Gery Smyth'}/>)

            })
            it('suggestion links are rendered', () => {
                expect(findLink(suggestions, 'gary')).to.have.length(1)
                expect(findLink(suggestions, 'smith')).to.have.length(1)
            })
        })
        context('some suggestions are found with different scores', () => {
            let suggestions
            beforeEach(() => {
                const suggestionsData =                         [
                    {
                        text: "gery",
                        options: [
                            {
                                "text": "gary",
                                "score": 0.8
                            }
                        ]
                    },
                    {
                        text: "smyth",
                        options: [
                            {
                                "text": "smith",
                                "score": 0.9
                            }
                        ]
                    }
                ]
                suggestions = shallow(<Suggestions suggestions={suggestionsData} search={search} searchTerm={'Gery Smyth'}/>)

            })
            it('suggestions are rendered in score order', () => {
                expect(suggestions.text()).to.eql('Did you mean smith gary?')
            })
        })
    })
    describe('clicking links', () => {
        it('clicking a suggestion calls search with replaced word', () => {
            const suggestionsData =                         [
                {
                    text: "gery",
                    options: [
                        {
                            "text": "gary",
                            "score": 0.8
                        }
                    ]
                },
                {
                    text: "smyth",
                    options: [
                        {
                            "text": "smith",
                            "score": 0.7
                        }
                    ]
                }
            ]

            const suggestions = shallow(<Suggestions suggestions={suggestionsData} search={search} searchTerm={'Gery Smyth'}/>)

            clickLink(suggestions, 'gary');
            expect(search).to.be.calledWith('gary Smyth')

            clickLink(suggestions, 'smith');
            expect(search).to.be.calledWith('Gery smith')
        })
    })

})

const findLink = (wrapper, text) => wrapper.find({children: text})
const clickLink = (wrapper, text) => findLink(wrapper, text).simulate('click')

