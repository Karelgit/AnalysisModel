package util;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.List;

/**
 * Created by Administrator on 2016/7/21.
 */
public class AnsjTest {
    public static void main(String[] args) {
        List<Term> parse = ToAnalysis.parse("北京天安门1231·repository广场");
        for (Term term : parse) {

            System.out.println(term);
        }
    }
}
