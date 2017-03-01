public class GardenTest{
    static int totalHolesToDig = 5;

    public static void main(String[] args) {
        Garden garden = new Garden();

        Thread mary = new Thread(new Mary(garden));
        Thread benjamin = new Thread(new Benjamin(garden));
        Thread newton = new Thread(new Newton(garden));

        mary.start();
        benjamin.start();
        newton.start();
    }

    static class Newton implements Runnable {
        private final Garden garden;

        public Newton(Garden garden) {
            this.garden = garden;
        }

        @Override
        public void run() {
            while (garden.totalDug() <= totalHolesToDig) {
                try {
                    garden.startDigging();
                    dig();
                } catch (Exception e) {
                    System.out.println("Error digging");
                } finally  {
                    garden.doneDigging();
                }
            }
        }

        private void dig() throws InterruptedException {
            System.out.println("Digging... \n Total holes dug is: " + garden.totalDug());
        }
    }

    protected static class Benjamin implements Runnable {
        private final Garden garden;

        public Benjamin(Garden garden) {
            this.garden = garden;
        }

        @Override
        public void run() {
            while (garden.totalSeeded() <= totalHolesToDig) {
                try  {
                    garden.startSeeding();
                    plant();
                } catch (Exception e) {
                    System.out.println("Error seeding");
                } finally {
                    garden.doneSeeding();
                }
            }
        }

        private void plant() throws InterruptedException {
            System.out.println("Seeding... \n Total holes seeded is: " + garden.totalSeeded());
        }
    }
    
    protected static class Mary implements Runnable {
        private final Garden garden;

        public Mary(Garden garden) {
            this.garden = garden;
        }

        @Override
        public void run() {
            while (garden.totalFilled() <= totalHolesToDig) {
                try {
                    garden.startFilling();
                    fill();
                } catch (Exception e) {
                    System.out.println("Error filling");
                } finally {
                    garden.doneFilling();
                }
            }
        }

        private void fill(){
            System.out.println("Filling... \n Total holes filled is: " + garden.totalFilled());
        }
    }

}