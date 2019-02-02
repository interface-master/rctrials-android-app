package ca.interfacemaster.surveyor;

public class Answer {
    private int qid; // question ID
    private String answer;

    public Answer() {}
    public Answer(int qid) {
        this.qid = qid;
    }
    public Answer(int qid, String answer) {
        this.qid = qid;
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
