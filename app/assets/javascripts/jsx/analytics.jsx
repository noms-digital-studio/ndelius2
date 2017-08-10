class ResultCount extends React.Component {

    render() {
        return (
            <p className="margin-top medium">Showing data from {this.props.total} events.</p>
        );
    }
}

class AnalyticsPanel extends React.Component {

    constructor() {
        super();

        this.state = {
            labels: [],
            data: []
        };
    }

    render() {

        return (
            <div>
                <ResultCount total={ _.reduce(this.state.data, (x, y) => x + y, 0) }/>
                <canvas id="barChart"/>
            </div>
        );
    }

    componentDidMount() {

        $.getJSON('/analytics/pageVisits', data => {
            this.setState({
                labels: _.keys(data),
                data: _.values(data)
            });
        });
    }

    componentDidUpdate() {

        var ctx = document.getElementById('barChart').getContext('2d');

        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: this.state.labels,
                datasets: [{
                    label: 'Page Visits',
                    data: this.state.data,
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
