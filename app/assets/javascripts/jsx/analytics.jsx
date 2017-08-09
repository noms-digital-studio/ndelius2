class ResultCount extends React.Component {

    render() {
        return (
            <p className="margin-top medium">Showing data from {this.props.results.length} events.</p>
        );
    }
}

class AnalyticsPanel extends React.Component {

    constructor() {
        super();
        this.state = {
            results: []
        };
    }

    componentWillMount() {

        $.getJSON('/analytics/recent/10', data => {
            this.setState({
                results: data.map(item => {
                    console.info(item);
                    item.dateTime = new Date(item.dateTime);
                    return item;
                })
            });
        });
    }

    componentDidMount() {

        // @TODO: Populate this chart with data
        var ctx = document.getElementById('barChart').getContext('2d'),
            barChart,
            chartLabels = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11'],
            chartData = [1, 1, 7, 2, 5, 6, 2, 2];

        barChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: chartLabels,
                datasets: [{
                    label: 'Page Visits',
                    data: chartData,
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
        });
    }

    render() {

        return (
            <div>
                <ResultCount {...this.state}/>
                <canvas id="barChart"/>
            </div>
        );
    }
}

class NavigationPanel extends React.Component {

    render() {
        return (
            <nav className="js-stick-at-top-when-scrolling">
                <div className="nav-header"/>
                <h3 className="heading-medium no-margin-top no-margin-bottom">Analytics options</h3>
                <a href="#" className="nav-item">Some option</a><br/>
                <a href="#" className="nav-item">Some option</a><br/>
                <a href="#" className="nav-item">Some option</a>
            </nav>
        );
    }
}

ReactDOM.render(
    <div className="grid-row">
        <div className="column-two-thirds">
            <AnalyticsPanel/>
        </div>
        <div className="column-one-third">
            <NavigationPanel/>
        </div>
    </div>,
    document.getElementById('react')
);
