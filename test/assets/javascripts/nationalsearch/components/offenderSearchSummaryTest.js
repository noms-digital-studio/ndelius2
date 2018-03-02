import OffenderSearchSummary, {Address}  from './offenderSearchSummary'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {offender} from '../test-helper'


describe('OffenderSearchSummary component', () => {
    it('should render offender summary elements', () => {

        const offenderSummary = offender({
            aliases: [{
                surname: "Marke"
            }],
            addresses:[
                {
                    streetName: "London Rd",
                    town: "Leicester",
                    county: "Leicestershire",
                    postcode:  "LE2 2PJ"
                }
            ],
            highlight: {
                "middleNames" : [ "Bobby", "Jim" ],
                "previousSurnames" : [ "Bobby"],
                "offenderAliases.surname" : [ "Marke" ],
                "contactDetails.addresses.postcode" : [ "LE2 2PJ" ],
                "contactDetails.addresses.streetName" : [ "London Rd" ],
                "contactDetails.addresses.town" : [ "Leicester" ],
                "contactDetails.addresses.county" : [ "Leicestershire" ]
            }
        })
        const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Mr Bean'}/>)

        expect(summary.find('Connect(OffenderSummaryTitle)').exists()).to.be.true
        expect(summary.find('CurrentOffender').exists()).to.be.true
        expect(summary.find({text: 'Marke'}).exists()).to.be.true
        expect(summary.find('PreviousSurname').exists()).to.be.true
        expect(summary.find('Address').exists()).to.be.true
        expect(summary.find('Connect(AddContactLink)').exists()).to.be.true
    })
    it('should not render optional offender summary elements when not flagged as highlighted', () => {

        const offenderSummary = offender({
            aliases: [{
                surname: "Marke"
            }],
            addresses:[
                {
                    streetName: "London Rd",
                    town: "Leicester",
                    county: "Leicestershire",
                    postcode:  "LE2 2PJ"
                }
            ],
            highlight: {
            }
        })
        const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Mr Bean'}/>)

        expect(summary.find('Connect(OffenderSummaryTitle)').exists()).to.be.true
        expect(summary.find('CurrentOffender').exists()).to.be.true
        expect(summary.find({text: 'Marke'}).exists()).to.be.false
        expect(summary.find('PreviousSurname').exists()).to.be.false
        expect(summary.find('Address').exists()).to.be.false
        expect(summary.find('Connect(AddContactLink)').exists()).to.be.true
    })
    describe('pnc display', () => {
        context('pnc present and matches a search term', () => {
            it('pnc number is displayed', () => {
                const offenderSummary = offender({
                    otherIds: {
                        crn: 'X1234',
                        pncNumber: '1965/1234567X'
                    },
                    highlight: {
                        "otherIds.pncNumber" : [ "1965/1234567X" ]
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith 1965/1234567X'}/>)

                expect(summary.find('#pncNumber')).to.have.length(1)
                expect(summary.find('#pncNumber Connect(MarkableText)').prop('text')).to.equal('1965/1234567X')
            })
        })
        context('pnc present and does not match a search term', () => {
            it('pnc number is not displayed', () => {
                const offenderSummary = offender({
                    otherIds: {
                        crn: 'X1234',
                        pncNumber: '1965/1234567X'
                    },
                    highlight: {
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith'}/>)

                expect(summary.find('#pncNumber')).to.have.length(0)
            })
        })
        context('pnc not present', () => {
            it('pnc number is not displayed', () => {
                const offenderSummary = offender({
                    otherIds: {
                        crn: 'X1234'
                    },
                    highlight: {
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith 1965/1234567X'}/>)

                expect(summary.find('#pncNumber')).to.have.length(0)
            })
        })
    })
    describe('noms display', () => {
        context('noms present and matches a search term', () => {
            it('noms number is displayed', () => {
                const offenderSummary = offender({
                    otherIds: {
                        crn: 'X1234',
                        nomsNumber: 'A1234AA'
                    },
                    highlight: {
                        "otherIds.nomsNumber" : [ "A1234AA" ]
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith A1234AA'}/>)

                expect(summary.find('#nomsNumber')).to.have.length(1)
                expect(summary.find('#nomsNumber Connect(MarkableText)').prop('text')).to.equal('A1234AA')
            })
        })
        context('noms present and does not match a search term', () => {
            it('noms number is not displayed', () => {
                const offenderSummary = offender({
                    otherIds: {
                        crn: 'X1234',
                        nomsNumber: 'A1234AA'
                    },
                    highlight: {
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith'}/>)

                expect(summary.find('#nomsNumber')).to.have.length(0)
            })
        })
        context('noms not present', () => {
            it('noms number is not displayed', () => {
                const offenderSummary = offender({
                    otherIds: {
                        crn: 'X1234'
                    },
                    highlight: {
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith A1234AA'}/>)

                expect(summary.find('#nomsNumber')).to.have.length(0)
            })
        })
    })
    describe('ni display', () => {
        context('ni present and matches a search term', () => {
            it('ni number is displayed', () => {
                const offenderSummary = offender({
                    otherIds: {
                        crn: 'X1234',
                        niNumber: 'NE998877A'
                    },
                    highlight: {
                        "otherIds.niNumber" : [ "NE998877A" ]
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith NE998877A'}/>)

                expect(summary.find('#niNumber')).to.have.length(1)
                expect(summary.find('#niNumber Connect(MarkableText)').prop('text')).to.equal('NE998877A')
            })
        })
        context('ni present and does not match a search term', () => {
            it('ni number is not displayed', () => {
                const offenderSummary = offender({
                    otherIds: {
                        crn: 'X1234',
                        niNumber: 'NE998877A'
                    },
                    highlight: {
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith'}/>)

                expect(summary.find('#niNumber')).to.have.length(0)
            })
        })
        context('ni not present', () => {
            it('ni number is not displayed', () => {
                const offenderSummary = offender({
                    otherIds: {
                        crn: 'X1234'
                    },
                    highlight: {
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith NE998877A'}/>)

                expect(summary.find('#niNumber')).to.have.length(0)
            })
        })
    })
    describe('cro display', () => {
        context('cro present and matches a search term', () => {
            it('cro number is displayed', () => {
                const offenderSummary = offender({
                    otherIds: {
                        crn: 'X1234',
                        croNumber: '123456/12A'
                    },
                    highlight: {
                        "otherIds.croNumber" : [ "123456/12A" ]
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith 123456/12A'}/>)

                expect(summary.find('#croNumber')).to.have.length(1)
                expect(summary.find('#croNumber Connect(MarkableText)').prop('text')).to.equal('123456/12A')
            })
        })
        context('cro present and does not match a search term', () => {
            it('cro number is not displayed', () => {
                const offenderSummary = offender({
                    otherIds: {
                        crn: 'X1234',
                        croNumber: '123456/12A'
                    },
                    highlight: {
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith'}/>)

                expect(summary.find('#croNumber')).to.have.length(0)
            })
        })
        context('cro not present', () => {
            it('cro number is not displayed', () => {
                const offenderSummary = offender({
                    otherIds: {
                        crn: 'X1234'
                    },
                    highlight: {
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith 123456/12A'}/>)

                expect(summary.find('#croNumber')).to.have.length(0)
            })
        })
    })
    describe('Middle names display', () => {
        context('Middle names present and matches a search term', () => {
            it('Middle names are displayed', () => {
                const offenderSummary = offender({
                    middleNames: ['Bob Jon'],
                    highlight: {
                        "middleNames" : [ "Bob" ]
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith Jon'}/>)

                expect(summary.find('MiddleNames')).to.have.length(1)
            })
        })
        context('Middle names present and does not match a search term', () => {
            it('Middle names not displayed', () => {
                const offenderSummary = offender({
                    middleNames: ['Bob Jon'],
                    highlight: {
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith'}/>)

                expect(summary.find('MiddleNames')).to.have.length(0)
            })
        })
        context('Middle names not present', () => {
            it('Middle names not displayed', () => {
                const offenderSummary = offender({
                    middleNames: [],
                    highlight: {
                    }
                })
                const summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith Fred'}/>)

                expect(summary.find('MiddleNames')).to.have.length(0)
            })
        })
    })
    describe('offender image display', () => {
        let summary
        context('has a noms image', () => {
            beforeEach(() => {
                const offenderSummary = offender({
                    oneTimeNomisRef: 'IMAGEREF'
                })
                summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith Fred'}/>)
            })

            it('renders image tag with noms image url', () => {
                expect(summary.find({
                    src: `offender/oneTimeNomisRef/IMAGEREF/image`
                }).exists()).to.be.true
            })
        })
        context('has no noms image', () => {
            beforeEach(() => {
                const offenderSummary = offender()
                delete offenderSummary.oneTimeNomisRef
                summary = shallow(<OffenderSearchSummary offenderSummary={offenderSummary} searchTerm={'Smith Fred'}/>)
            })
            it('renders image tag with blank image url', () => {
                expect(summary.find({
                    src: 'assets/images/NoPhoto@2x.png'
                }).exists()).to.be.true
            })

        })
    })
})

describe('Address component', () => {
    const extractLines = address => address.find('Connect(MarkableText)').map(text => text.prop('text'))

    context('with all address lines', () => {
        it('all lines rendered with address number concatenated with street name', () => {
            const address = shallow(<Address address={{
                buildingName: 'Big Building',
                addressNumber: '99',
                streetName: 'High Street',
                town: 'Sheffield',
                county: 'South Yorkshire',
                postcode: 'S1 2BX',
            }}/>)

            const lines = extractLines(address)

            expect(lines).to.eql(
                ['Big Building', '99 High Street', 'Sheffield', 'South Yorkshire', 'S1 2BX']
            )
        })
    })
    context('with streetName but no addressNumber', () => {
        it('streetName rendered on its own with no white space padding', () => {
            const address = shallow(<Address address={ {
                "streetName": "Foo Street"
            }}/>)

            const lines = extractLines(address)

            expect(lines).to.eql(
                ['Foo Street']
            )
        })
    })
    context('with no streetName but an addressNumber', () => {
        it('addressNumber rendered on its own with no white space padding', () => {
            const address = shallow(<Address address={ {
                "addressNumber": "42"
            }}/>)

            const lines = extractLines(address)

            expect(lines).to.eql(
                ['42']
            )
        })
    })
    context('with many address lines missing', () => {
        it('lines removed but with address number concatenated with street name', () => {
            const address = shallow(<Address address={{
                buildingName: '',
                addressNumber: '99',
                streetName: 'High Street',
                town: null,
                postcode: 'S1 2BX',
            }}/>)

            const lines = extractLines(address)

            expect(lines).to.eql(
                ['99 High Street', 'S1 2BX']
            )
        })
    })
})
