import PropTypes from 'prop-types'
import {Component} from 'react'

class AnalyticsBarChart extends Component {
    constructor(props) {
        super(props);
    }
    render() {
        const {description} = this.props
        return (
            <div style={{float: 'left', margin: '10px', backgroundColor: '#f8f8f8', padding: '10px', minWidth: '440px', minHeight: '300px'}}>
                <p style={{fontSize: '16px', textAlign: 'center', margin: '10px'}}>{description}</p>

                <canvas ref={(canvas) => { this.canvas = canvas; }}/>
            </div>
        )
    }
    componentDidUpdate() {
        new Chart(this.canvas.getContext('2d'), chartOptions(this.props));
    }
}


export const chartOptions = props => {
    const {label, numberToCountData} = props

    return {
        type: 'bar',
        data: {
            labels: Object.getOwnPropertyNames(numberToCountData),
            datasets: [{
                label,
                data: Object.getOwnPropertyNames(numberToCountData).map(property => numberToCountData[property]),
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            }
        }
    }

}


AnalyticsBarChart.propTypes = {
    description: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    fetching: PropTypes.bool.isRequired,
    numberToCountData: PropTypes.object.isRequired
};


export default AnalyticsBarChart;
