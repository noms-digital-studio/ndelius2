class AnalyticsPanel extends React.Component {

    render() {

        $.getJSON('/spellcheck/' + this.state.name, data => {
            this.setState({
                results: data
            });
        });

        return (
            <div>
                Analytics
            </div>
        );
    }
}

ReactDOM.render(
    <AnalyticsPanel />,
    document.getElementById('content')
);
