import {Component} from 'react'
import GovUkPhaseBanner from './govukPhaseBanner';
import PropTypes from 'prop-types'
import moment from 'moment'
import {Link} from 'react-router'

class SatisfactionPage extends Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        fetch(this.props);
        const interval = setInterval(() => fetch(this.props), 60000);
        if (interval.unref) {interval.unref()} // when running in mocha/node unref so test doesn't hang
    }

    render() {
        return (
            <div>
                <GovUkPhaseBanner basicVersion={true}/>
                <h1 className="heading-xlarge no-margin-bottom">National Search Satisfaction</h1>
                <div className="grid-row margin-top">


                    <div className="column-two-thirds">
                        <div style={{float: 'left', margin: '10px', backgroundColor: '#cccccc', padding: '10px', minWidth: '600px', minHeight: '300px'}}>
                            <p id='description' style={{fontSize: '16px', textAlign: 'center', margin: '10px'}}>Weekly Satisfaction Counts</p>
                            <canvas style={{backgroundColor: '#cccccc'}} ref={(canvas) => { this.canvas = canvas; }}/>
                        </div>

                    </div>
                    <div className="column-one-third">
                        <nav className="js-stick-at-top-when-scrolling">
                            <div className="nav-header"/>
                            <h3 className="heading-medium no-margin-top no-margin-bottom">Links for</h3>
                            <a tabIndex={1} href='javascript:' style={{fontWeight: toFontWeight(this.props.yearNumber, '2018')}} className="clickable" onClick={() => this.props.changeYear('2018')}>2018</a><br/>
                            <a tabIndex={2} href='javascript:' style={{fontWeight: toFontWeight(this.props.yearNumber, '2019')}} className="clickable" onClick={() => this.props.changeYear('2019')}>2019</a><br/>
                            <a tabIndex={3} href='javascript:' style={{fontWeight: toFontWeight(this.props.yearNumber, '2020')}} className="clickable" onClick={() => this.props.changeYear('2020')}>2020</a><br/>
                            <a tabIndex={4} href='javascript:' style={{fontWeight: toFontWeight(this.props.yearNumber, '2021')}} className="clickable" onClick={() => this.props.changeYear('2021')}>2021</a><br/>
                            <a tabIndex={5} href='javascript:' style={{fontWeight: toFontWeight(this.props.yearNumber, '2022')}} className="clickable" onClick={() => this.props.changeYear('2022')}>2022</a><br/>
                            <a href="../feedback/nationalSearch">National search feedback</a><br/>
                            <Link to ='/analytics' >National search analytics</Link>
                        </nav>
                        <input className="button margin-top" type="button" value="Refresh" onClick={() => fetch(this.props)}/>
                    </div>

                </div>
            </div>)
    }

    componentDidUpdate() {
        if (this.chart) {
            this.chart.destroy()
        }

        this.chart = new Chart(this.canvas.getContext('2d'), chartOptions(this.props.satisfactionCounts, this.props.yearNumber));
    }

}

const convertCountsToMap = function (countsForRating) {
    const countsAsMap = {};
    countsForRating.forEach(data => {
        countsAsMap[data.yearAndWeek] = data.count
    });
    return countsAsMap;
};

export const ratingData = function (countsForRating, currentWeekNumber, yearNumber) {
    if (!countsForRating) return [];

    const countsAsMap = convertCountsToMap(countsForRating);

    const weeklyRatingData = [];
    for (let weekNumber = 1; weekNumber <= currentWeekNumber; weekNumber++) {
        const key = yearNumber
                        + '-'
                        + (weekNumber - 1); // MongoDB $week numbers start at 0 unlike moment.js which starts at 1
        if (countsAsMap[key]) {
            weeklyRatingData.push(countsAsMap[key])
        } else {
            weeklyRatingData.push(0)
        }

    }
    return weeklyRatingData;
};

export const generateXAxisLabels = function (yearNumber, currentWeekNumber) {
    const labels = [];
    for (let weekNumber = 1; weekNumber <= currentWeekNumber; weekNumber++) {
        labels.push(yearNumber + '-' + weekNumber)
    }
    return labels;
};

const chartOptions = (satisfactionCounts, yearNumber) => {
    const currentWeekNumber = moment().utc().week();
    const labels = generateXAxisLabels(yearNumber, currentWeekNumber);

    return {
        type: 'line',
        data: {
            labels,
            datasets: [
                {
                    label: 'Very Satisfied',
                    data: ratingData(satisfactionCounts['Very satisfied'], currentWeekNumber, yearNumber),
                    backgroundColor: '#00ff00',
                    borderColor: '#00ff00',
                    fill: false,
                    lineTension: 0,
                    borderWidth: 3
                },
                {
                    label: 'Satisfied',
                    data: ratingData(satisfactionCounts['Satisfied'], currentWeekNumber, yearNumber),
                    backgroundColor: '#FFFF00',
                    borderColor: '#FFFF00',
                    fill: false,
                    lineTension: 0,
                    borderWidth: 3
                },
                {
                    label: 'Neither',
                    data: ratingData(satisfactionCounts['Neither'], currentWeekNumber, yearNumber),
                    backgroundColor: '#FFCC00',
                    borderColor: '#FFCC00',
                    fill: false,
                    lineTension: 0,
                    borderWidth: 3
                },
                {
                    label: 'Dissatisfied',
                    data: ratingData(satisfactionCounts['Dissatisfied'], currentWeekNumber, yearNumber),
                    backgroundColor: '#FF7F00',
                    borderColor: '#FF7F00',
                    fill: false,
                    lineTension: 0,
                    borderWidth: 3
                },
                {
                    label: 'Very Dissatisfied',
                    data: ratingData(satisfactionCounts['Very dissatisfied'], currentWeekNumber, yearNumber),
                    backgroundColor: '#FF0000',
                    borderColor: '#FF0000',
                    fill: false,
                    lineTension: 0,
                    borderWidth: 3
                },

            ]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        reverse: false,
                        beginAtZero: true
                    }
                }]
            }

        }
    }
};

const fetch = props => {
    const {fetchSatisfactionCounts} = props;
    fetchSatisfactionCounts()
};

SatisfactionPage.propTypes = {
    fetchSatisfactionCounts: PropTypes.func.isRequired,
    changeYear: PropTypes.func.isRequired
};

const toFontWeight = (yearNumber, currentYearNumber) => yearNumber === currentYearNumber ? 'bold' : 'normal';

export default SatisfactionPage;