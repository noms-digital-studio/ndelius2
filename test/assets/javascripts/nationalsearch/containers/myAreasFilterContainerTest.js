import { extractMyProbationAreas } from './myAreasFilterContainer'
import { expect } from 'chai'

describe('myAreasFilterContainer', () => {
  let myProbationAreas
  let probationAreasFilter

  describe('extractMyProbationAreas', () => {
    context('empty my probation areas ', () => {
      beforeEach(() => {
        probationAreasFilter = [
          { code: 'N01', description: 'Area for N01', count: 8 },
          { code: 'N02', description: 'Area for N02', count: 9 }
        ]
        myProbationAreas = {}
      })

      it('filterValues is empty list', () => {
        expect(extractMyProbationAreas(probationAreasFilter, myProbationAreas)).to.eql([])
      })
    })

    context('aggregation with filters that exist in myProbationAreas', () => {
      beforeEach(() => {
        probationAreasFilter = [
          { code: 'N01', description: 'Area for N01', count: 8 },
          { code: 'N02', description: 'Area for N02', count: 9 }
        ]
        myProbationAreas = { 'N01': 'Area for N01' }
      })

      it('myProbationAreas has single item with count', () => {
        expect(extractMyProbationAreas(probationAreasFilter, myProbationAreas)).to.eql([{
          code: 'N01',
          description: 'Area for N01',
          count: 8
        }])
      })
    })

    context('many aggregation with filters that exist in myProbationAreas', () => {
      beforeEach(() => {
        probationAreasFilter = [
          { code: 'N01', description: 'Z Area for N01', count: 8 },
          { code: 'N02', description: 'a Area for N02', count: 9 },
          { code: 'N03', description: 'B Area for N03', count: 10 }
        ]
        myProbationAreas = { 'N01': 'Z Area for N01', 'N02': 'a Area for N02', 'N03': 'B Area for N03' }
      })

      it('myProbationAreas is sorted', () => {
        expect(extractMyProbationAreas(probationAreasFilter, myProbationAreas)).to.eql([
          { code: 'N02', description: 'a Area for N02', count: 9 },
          { code: 'N03', description: 'B Area for N03', count: 10 },
          { code: 'N01', description: 'Z Area for N01', count: 8 }
        ])
      })
    })

    context('aggregation with filters that do not exist in myProbationAreas', () => {
      beforeEach(() => {
        probationAreasFilter = [
          { code: 'N01', description: 'Area for N01', count: 8 },
          { code: 'N02', description: 'Area for N02', count: 9 }
        ]
        myProbationAreas = { 'N03': 'Area for N03', 'N04': 'Area for N04' }
      })

      it('myProbationAreas has my probation areas with zero counts', () => {
        expect(extractMyProbationAreas(probationAreasFilter, myProbationAreas)).to.eql([{
          code: 'N03',
          description: 'Area for N03',
          count: 0
        }, { code: 'N04', description: 'Area for N04', count: 0 }])
      })
    })
  })
})
