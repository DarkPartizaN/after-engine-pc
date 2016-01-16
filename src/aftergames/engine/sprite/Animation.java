package aftergames.engine.sprite;

/**
 *
 * @author KiQDominaN
 */
public final class Animation {

    private AnimationSequence sequence;
    private float fps = 10f;
    private long last_time;
    private int current_frame = 0;
    private String id;

    public Animation() {
        id = "";
    }

    public Animation(String id) {
        this.id = id;
    }

    public Animation(String id, float fps) {
        this.id = id;

        setFps(fps);
    }

    public void setSequence(Animation anim) {
        resetSequence();

        sequence = anim.sequence;
    }

    public void setSequence(AnimationSequence seq) {
        resetSequence();

        sequence = seq;
    }

    public void setFps(float fps) {
        this.fps = fps;
    }

    public int getFrame(int num) {
        return sequence.getFrame(num);
    }

    public int getCurrentFrame() {
        return checkSequence();
    }

    public void setSequencePos(int pos) {
        current_frame = pos;
    }

    public int getSequencePos() {
        return current_frame;
    }

    public String getID() {
        return id;
    }

    public void end() {
        setSequencePos(sequence.getSize() - 1);
    }

    public boolean finished() {
        return current_frame == sequence.getSize() - 1;
    }

    private int checkSequence() {
        if (sequence == null || sequence.getSize() == 0) return 0;
        if (fps <= 0)
            return sequence.getFrame(current_frame);

        long current_time = System.currentTimeMillis();
        if (current_time - last_time > 1000f / fps) {
            current_frame++;
            if (current_frame >= sequence.getSize()) current_frame = 0;

            last_time = current_time;
        }

        return sequence.getFrame(current_frame);
    }

    public void resetSequence() {
        current_frame = 0;
    }

    public void nextFrame() {
        current_frame++;
        if (current_frame >= sequence.getSize()) current_frame = 0;
    }

    public void prevFrame() {
        current_frame--;
        if (current_frame < 0) current_frame = sequence.getSize() - 1;
    }

    public int getTotalFrames() {
        return sequence.getSize();
    }

    public static class AnimationSequence {

        private int[] frames;

        public AnimationSequence() {
            frames = new int[1];

            for (int i = 0; i < frames.length; i++) frames[i] = i;
        }

        public AnimationSequence(int[] sequence) {
            frames = sequence;
        }

        public int getSize() {
            return frames.length;
        }

        public int getFrame(int frame_number) {
            return frames[frame_number];
        }
    }

}
