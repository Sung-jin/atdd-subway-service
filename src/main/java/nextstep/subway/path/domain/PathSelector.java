package nextstep.subway.path.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathSelector {
    private static WeightedMultigraph<Station, DefaultWeightedEdge> graph
            = new WeightedMultigraph<>(DefaultWeightedEdge.class);
    private static DijkstraShortestPath<Station, DefaultWeightedEdge> path
            = new DijkstraShortestPath<>(graph);

    public static void init(List<Line> lines) {
        for (Line line : lines) {
            addSections(line.getSections());
        }
    }

    public static void add(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        graph.addVertex(upStation);
        graph.addVertex(downStation);
        graph.setEdgeWeight(graph.addEdge(upStation,downStation), section.getDistance().value());
    }

    public static void remove(Section section) {
        graph.removeEdge(section.getUpStation(), section.getDownStation());
    }

    public static PathResult select(Station source, Station target) {
        try {
            return new PathResult(path.getPath(source, target));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("연결되지 않은 역은 조회 할 수 없습니다.");
        }
    }

    public static void clear() {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        path = new DijkstraShortestPath<>(graph);
    }

    private static void addSections(Sections sections) {
        for (Section section : sections.getSections()) {
            add(section);
        }
    }
}