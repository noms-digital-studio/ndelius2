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
                const suggestions = shallow(<Suggestions suggestions={[]} search={search} searchTerm={'Gery Smyth'} probationAreasFilter={[]}/>)
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
                suggestions = shallow(<Suggestions suggestions={suggestionsData} search={search} searchTerm={'Gery Smyth'} probationAreasFilter={[]}/>)

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
                suggestions = shallow(<Suggestions suggestions={suggestionsData} search={search} searchTerm={'Gery Smyth'} probationAreasFilter={[]}/>)

            })
            it('suggestions are rendered in score order', () => {
                expect(suggestions.text()).to.eql('Did you mean smith gary?')
            })
        })
        context('too many some suggestions are found with different scores', () => {
            let suggestions
            beforeEach(() => {
                const suggestionsData =                         [
                    {
                        text: "gery",
                        options: [
                            {
                                "text": "gary1",
                                "score": 0.8
                            }
                        ]
                    },
                    {
                        text: "gery",
                        options: [
                            {
                                "text": "gary2",
                                "score": 0.7
                            }
                        ]
                    },
                    {
                        text: "gery",
                        options: [
                            {
                                "text": "gary3",
                                "score": 0.6
                            }
                        ]
                    },
                    {
                        text: "gery",
                        options: [
                            {
                                "text": "gary4",
                                "score": 0.5
                            }
                        ]
                    },
                    {
                        text: "gery",
                        options: [
                            {
                                "text": "gary5",
                                "score": 0.4
                            }
                        ]
                    },
                    {
                        text: "gery",
                        options: [
                            {
                                "text": "gary6",
                                "score": 0.3
                            }
                        ]
                    },
                ]
                suggestions = shallow(<Suggestions suggestions={suggestionsData} search={search} searchTerm={'Gery Smyth'} probationAreasFilter={[]}/>)

            })
            it('suggestions are rendered in score order', () => {
                expect(suggestions.text()).to.eql('Did you mean gary1 gary2 gary3 gary4?')
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

            const suggestions = shallow(<Suggestions suggestions={suggestionsData} search={search} searchTerm={'Gery Smyth'} probationAreasFilter={['N01']}/>)

            clickLink(suggestions, 'gary');
            expect(search.getCall(0).args[0]).to.equal('gary Smyth');
            expect(search.getCall(0).args[1]).to.eql(['N01']);

            clickLink(suggestions, 'smith');
            expect(search).to.be.calledWith('Gery smith')
            expect(search.getCall(1).args[0]).to.equal('Gery smith');
            expect(search.getCall(1).args[1]).to.eql(['N01']);
        })
    })

})

const findLink = (wrapper, text) => wrapper.find({children: text})
const clickLink = (wrapper, text) => findLink(wrapper, text).simulate('click')

