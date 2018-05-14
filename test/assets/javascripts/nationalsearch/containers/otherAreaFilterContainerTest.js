import {removeMyProbationAreas} from './otherAreasFilterContainer'
import {expect} from 'chai';


describe('areaFilterContainer', () => {
    let byProbationArea
    let myProbationAreas
    let probationAreasFilter

    describe('removeMyProbationAreas', () => {
        context('empty my probation areas ', () => {
            beforeEach(() => {
                byProbationArea = [
                    {code: 'N01', description: 'Area for N01', count: 8 },
                    {code: 'N02', description: 'Area for N02', count: 9 }
                ]
                myProbationAreas = {}
                probationAreasFilter = {}
            })

            it('byProbationArea remains unchanged', () => {
                expect(removeMyProbationAreas(byProbationArea, myProbationAreas, probationAreasFilter)).to.eql(byProbationArea)
            })
        })
        context('probation areas are sorted', () => {
            beforeEach(() => {
                byProbationArea = [
                    {code: 'N01', description: 'Z Area for N01', count: 8 },
                    {code: 'N02', description: 'a Area for N02', count: 9 },
                    {code: 'N03', description: 'B Area for N03', count: 10 },
                ]
                myProbationAreas = {}
                probationAreasFilter = {}
            })

            it('byProbationArea are sorted by description', () => {
                expect(removeMyProbationAreas(byProbationArea, myProbationAreas, probationAreasFilter)).to.eql([
                    {code: 'N02', description: 'a Area for N02', count: 9 },
                    {code: 'N03', description: 'B Area for N03', count: 10 },
                    {code: 'N01', description: 'Z Area for N01', count: 8 }
                ])
            })
        })
        context('aggregation with filters that exist in myProbationAreas', () => {
            beforeEach(() => {
                byProbationArea = [
                    {code: 'N01', description: 'Area for N01', count: 8 },
                    {code: 'N02', description: 'Area for N02', count: 9 }
                ]
                myProbationAreas = {'N01': 'Area for N01'}
                probationAreasFilter = {}
            })

            it('byProbationArea has my provider removed', () => {
                expect(removeMyProbationAreas(byProbationArea, myProbationAreas, probationAreasFilter)).to.eql([{code: 'N02', description: 'Area for N02', count: 9 }])
            })
        })
        context('with empty aggregation', () => {
            beforeEach(() => {
                byProbationArea = []
                myProbationAreas = {'N03': 'Area for N03', 'N04': 'Area for N04'}
                probationAreasFilter = {}
            })

            it('byProbationArea remains unchanged', () => {
                expect(removeMyProbationAreas(byProbationArea, myProbationAreas, probationAreasFilter)).to.eql([])
            })
        })
        context('with empty aggregation but with existing filter', () => {
            beforeEach(() => {
                byProbationArea = []
                myProbationAreas = {'N03': 'Area for N03', 'N04': 'Area for N04'}
                probationAreasFilter = {'N05': 'Area for N05'}
            })

            it('byProbationArea has a zero value from current filter', () => {
                expect(removeMyProbationAreas(byProbationArea, myProbationAreas, probationAreasFilter)).to.eql([{code: 'N05', description: 'Area for N05', count: 0 }])
            })
        })
        context('with empty aggregation but with existing filter that matches myProbationArea', () => {
            beforeEach(() => {
                byProbationArea = []
                myProbationAreas = {'N03': 'Area for N03', 'N04': 'Area for N04'}
                probationAreasFilter = {'N03': 'Area for N03'}
            })

            it('byProbationArea has remains unchanged', () => {
                expect(removeMyProbationAreas(byProbationArea, myProbationAreas, probationAreasFilter)).to.eql([])
            })
        })
    })

})

